// Flat ESLint config (ESLint 9) for the Firebase Functions (Node 20, CommonJS).
import js from "@eslint/js";
import globals from "globals";
import promise from "eslint-plugin-promise";

export default [
  {
    ignores: ["node_modules/**", "coverage/**"],
  },
  js.configs.recommended,
  {
    files: ["**/*.js"],
    languageOptions: {
      ecmaVersion: 2022,
      sourceType: "commonjs",
      globals: {
        ...globals.node,
      },
    },
    plugins: {
      promise,
    },
    rules: {
      "no-unused-vars": ["warn", { argsIgnorePattern: "^_" }],
      "no-console": "off",
      "promise/no-nesting": "warn",
    },
  },
  {
    // Vitest test files.
    files: ["**/*.test.js", "**/*.spec.js"],
    languageOptions: {
      globals: {
        ...globals.node,
      },
    },
  },
];
