// Unit tests for the AI concierge JSON parsing — no Anthropic key / network needed.
// Vitest test files run as ESM; the source under test is CommonJS (interop handled by Vitest).
import { describe, it, expect } from "vitest";
import concierge from "./concierge.js";

const { parseRecommendations } = concierge;

describe("parseRecommendations", () => {
  it("parses a clean JSON object", () => {
    const text = JSON.stringify({
      recommendations: [
        { title: "Dune: Part Two", reason: "Epic sci-fi.", genres: ["Sci-Fi"] },
        { title: "Whiplash", reason: "Intense drama." },
      ],
    });
    const recs = parseRecommendations(text);
    expect(recs).toHaveLength(2);
    expect(recs[0].title).toBe("Dune: Part Two");
    expect(recs[0].genres).toEqual(["Sci-Fi"]);
    expect(recs[1].genres).toEqual([]); // defaulted
  });

  it("extracts JSON when the model wraps it in prose / markdown fences", () => {
    const text =
      "Sure! Here are some picks:\n```json\n" +
      JSON.stringify({ recommendations: [{ title: "Parasite", reason: "x" }] }) +
      "\n```\nEnjoy!";
    const recs = parseRecommendations(text);
    expect(recs).toHaveLength(1);
    expect(recs[0].title).toBe("Parasite");
  });

  it("drops malformed entries lacking a title", () => {
    const text = JSON.stringify({
      recommendations: [{ reason: "no title" }, { title: "Heat", reason: "ok" }],
    });
    const recs = parseRecommendations(text);
    expect(recs).toHaveLength(1);
    expect(recs[0].title).toBe("Heat");
  });

  it("returns [] when recommendations is absent", () => {
    expect(parseRecommendations('{"foo": 1}')).toEqual([]);
  });

  it("throws when no JSON object is present", () => {
    expect(() => parseRecommendations("no json here")).toThrow();
  });

  it("throws on non-string input", () => {
    expect(() => parseRecommendations(null)).toThrow();
  });
});
