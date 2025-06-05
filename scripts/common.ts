export type Parameter = {
    name: string,
    in?: "query" | string,
    style?: "deepObject" | string,
    $ref?: string
}

export type Operation = {
    operationId: string;
    parameters?: Parameter[]
}

export type BundledSchema = {
    paths: {
        [key: string]: {
            [key: string]: Operation
        }
    }
    components?: {
        parameters?: {
            [key: string]: Parameter
        }
    }
}
