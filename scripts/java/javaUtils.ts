import { openFile, getPathCmdParameter as getGenericPathCmdParameter } from "../utils";

export function openJavaFile(path: string): string {
    return openFile(path, "java");
}

export function getJavaPathCmdParameter(): string {
    return getGenericPathCmdParameter("java");
}
