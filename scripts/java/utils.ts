import fs from "fs";
import dotenv from "dotenv";

export function loadDotenv() {
    dotenv.config();
}

export function openJavaFile(path: string): string {
    if (!path.includes(".java")) {
        throw new Error("Path must lead to a java file!");
    }

    return fs.readFileSync(path, "utf-8");
}

export function getPathCmdParameter(): string {
    const pathArg = process.argv.find((val) => val.includes("--path="));

    if (pathArg == null) {
        throw new Error(
            `Undefined path! Don't forget to specify the path, e.g. "...fixFileRelatedGetRequests.ts ./TargetFile.java"`
        );
    }

    const path = pathArg?.replace("--path=", "");
    if (!path.includes(".java")) {
        throw new Error("Path must lead to a java file!");
    }

    return path;
}
