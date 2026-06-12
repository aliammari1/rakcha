/**
 * AI Cinema Concierge / Film Recommender (Firebase callable).
 *
 * Built on the deps already present in this project:
 *   @langchain/langgraph  — orchestrates a small recommend graph
 *   @langchain/anthropic  — ChatAnthropic, model `claude-haiku-4-5`
 *
 * Consumed independently by:
 *   - the FlutterFlow mobile app  (lib/concierge/concierge_client.dart)
 *   - the Symfony/Twig web app     (src/Service/ConciergeClient.php)
 * via a direct HTTPS fetch to this callable — there is NO shared REST API.
 *
 * The model returns strict JSON: { "recommendations": [{title, reason, genres}] }.
 *
 * Set the Anthropic key before deploying:
 *   firebase functions:config:set anthropic.key="sk-ant-..."   (gen-1 config)
 *   — or set the ANTHROPIC_API_KEY env var for the function.
 */
const functions = require("firebase-functions");
const { ChatAnthropic } = require("@langchain/anthropic");
const { StateGraph, Annotation, START, END } = require("@langchain/langgraph");

const SYSTEM_PROMPT = [
  "You are RAKCHA's cinema concierge — an expert film recommender for a",
  "cinema-management platform. Given a viewer's request and optional favorite",
  "genres, suggest 3 to 5 films they would enjoy.",
  "",
  "Reply with STRICT JSON only (no markdown, no prose) in exactly this shape:",
  '{ "recommendations": [ { "title": string, "reason": string, "genres": string[] } ] }',
  "Keep each reason to one or two sentences and tailor it to the request.",
].join("\n");

/** Resolve the Anthropic API key from env or gen-1 functions config. */
function resolveApiKey() {
  if (process.env.ANTHROPIC_API_KEY) return process.env.ANTHROPIC_API_KEY;
  try {
    const cfg = functions.config();
    if (cfg && cfg.anthropic && cfg.anthropic.key) return cfg.anthropic.key;
  } catch (_) {
    /* functions.config() unavailable outside the runtime */
  }
  return undefined;
}

/** Lazily build the model so unit tests can import this module without a key. */
function buildModel() {
  return new ChatAnthropic({
    model: "claude-haiku-4-5",
    maxTokens: 1024,
    apiKey: resolveApiKey(),
  });
}

/**
 * Extracts the first JSON object from a model response and validates the
 * recommendations array. Exported for unit testing (see concierge.test.js).
 */
function parseRecommendations(text) {
  if (typeof text !== "string") {
    throw new Error("Model returned non-text content");
  }
  const start = text.indexOf("{");
  const end = text.lastIndexOf("}");
  if (start === -1 || end === -1 || end < start) {
    throw new Error("No JSON object found in model response");
  }
  const parsed = JSON.parse(text.slice(start, end + 1));
  const recs = Array.isArray(parsed.recommendations)
    ? parsed.recommendations
    : [];
  return recs
    .filter((r) => r && typeof r.title === "string")
    .map((r) => ({
      title: r.title,
      reason: typeof r.reason === "string" ? r.reason : "",
      genres: Array.isArray(r.genres) ? r.genres.map(String) : [],
    }));
}

/** Builds the LangGraph recommend graph. Each node is a pure-ish async step. */
function buildGraph(model) {
  const State = Annotation.Root({
    prompt: Annotation(),
    favoriteGenres: Annotation(),
    recommendations: Annotation(),
  });

  async function recommend(state) {
    const genres =
      Array.isArray(state.favoriteGenres) && state.favoriteGenres.length
        ? `Favorite genres: ${state.favoriteGenres.join(", ")}.`
        : "No favorite genres provided.";
    const response = await model.invoke([
      { role: "system", content: SYSTEM_PROMPT },
      { role: "user", content: `${state.prompt}\n\n${genres}` },
    ]);
    const text =
      typeof response.content === "string"
        ? response.content
        : JSON.stringify(response.content);
    return { recommendations: parseRecommendations(text) };
  }

  return new StateGraph(State)
    .addNode("recommend", recommend)
    .addEdge(START, "recommend")
    .addEdge("recommend", END)
    .compile();
}

/**
 * Callable entry point. Request data: { prompt: string, favoriteGenres?: string[] }.
 * Response: { recommendations: [{ title, reason, genres }] }.
 */
const cinemaConcierge = functions.https.onCall(async (data) => {
  const prompt = (data && data.prompt ? String(data.prompt) : "").trim();
  if (!prompt) {
    throw new functions.https.HttpsError(
      "invalid-argument",
      "A non-empty 'prompt' is required.",
    );
  }
  if (!resolveApiKey()) {
    throw new functions.https.HttpsError(
      "failed-precondition",
      "Anthropic API key is not configured.",
    );
  }

  const favoriteGenres = Array.isArray(data && data.favoriteGenres)
    ? data.favoriteGenres.map(String)
    : [];

  try {
    const graph = buildGraph(buildModel());
    const result = await graph.invoke({ prompt, favoriteGenres });
    return { recommendations: result.recommendations };
  } catch (err) {
    throw new functions.https.HttpsError(
      "internal",
      `Concierge failed: ${err.message}`,
    );
  }
});

module.exports = { cinemaConcierge, parseRecommendations, buildGraph };
