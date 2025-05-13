import { openFile, getPathCmdParameter as getGenericPathCmdParameter } from "../utils";

export function openPythonFile(path: string): string {
    return openFile(path, "py");
}

export function getPythonPathCmdParameter(): string {
    return getGenericPathCmdParameter("py");
} 