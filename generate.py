import os
import json
import sys
import re
import ast
import astor

python_bearer_auth = {
    "type": "apiKey",
    "in": "header",
    "name": "Authorization"
}

default_bearer_auth = {
    "type": "http",
    "scheme": "bearer",
    "bearerFormat": "JWT"
}

lang = "python"


def change_bearer(bearer=python_bearer_auth):
    with open('openapi.json', encoding='utf8') as file:
        j = json.load(file)

        j["components"]["securitySchemes"]["bearerAuth"] = bearer

    with open('openapi.json', 'w', encoding='utf8') as file:
        json.dump(j, file, indent=4, ensure_ascii=False)


def change_default_header_for_java():
    file_name = 'unit/src/main/java/io/swagger/client/ApiClient.java'

    # Read the Java file
    with open(file_name, 'r') as java_file:
        java_code = java_file.read()

    # Define the method name you want to modify
    method_name_to_modify = 'selectHeaderContentType'

    # Define the additional code you want to add
    additional_code = ' if (defaultHeaderMap.get("Content-Type") != null){\n' \
                      ' return defaultHeaderMap.get("Content-Type");}\n'

    # Regular expression pattern to match the method
    pattern = r'(\s*public\s.*\s*' + re.escape(method_name_to_modify) + '\s*\([^\)]*\)\s*\{)([^}]*)\}'

    # Find and modify the method in the Java code
    def replace_method(match):
        method_declaration, method_body = match.groups()
        modified_method = f'{method_declaration}{method_body}{additional_code}\n}}'
        return modified_method

    modified_java_code = re.sub(pattern, replace_method, java_code, flags=re.DOTALL)

    # Write the modified code back to the file
    with open(file_name, 'w') as java_file:
        java_file.write(modified_java_code)


def change_default_header_for_python():
    file_name = 'unit/swagger_client/api_client.py'

    # Read the Python file
    with open(file_name, 'r') as python_file:
        python_code = python_file.read()

    # Parse the Python code
    parsed_code = ast.parse(python_code)

    # Define the function name you want to modify
    function_name_to_modify = 'select_header_content_type'

    # Iterate through the syntax tree to find the function
    for node in ast.walk(parsed_code):
        if isinstance(node, ast.FunctionDef) and node.name == function_name_to_modify:
            # Modify the function by adding code
            new_code = ast.parse('if self.default_headers.get("Content-Type"): \n'
                                 '\treturn self.default_headers.get("Content-Type")\n').body[0]
            node.body.append(new_code)

    # Convert the modified syntax tree back to Python code
    modified_code = astor.to_source(parsed_code)

    # Write the modified code back to the file
    with open(file_name, 'w') as python_file:
        python_file.write(modified_code)


if __name__ == "__main__":
    if len(sys.argv) > 1:
        lang = sys.argv[1]

    if lang == "python":
        change_bearer()

    path_of_the_directory = './schemas/'

    for filename in os.listdir(path_of_the_directory):
        f_path = os.path.join(path_of_the_directory, filename)
        if os.path.isfile(f_path):
            f = open(f_path, 'r', encoding="utf8")
            json_object = json.load(f)
            f.close

            json_object_as_string = str(json_object)
            json_object_as_string = json_object_as_string.replace('./schemas/', '')
            json_object_as_string = json_object_as_string.replace('./', '')
            json_object_as_string = json_object_as_string.replace('"$ref": "../', '"$ref": "')

            if json_object_as_string == str(json_object):
                continue

            data = eval(json_object_as_string)

            # Re-open file here
            f2 = open(f_path, 'w', encoding="utf8")
            json.dump(data, f2, indent=4)
            f2.close()

    if lang == 'python':
        path_of_the_directory = "./unit/swagger_client"
        for root, dirs, files in os.walk(path_of_the_directory, topdown=False):
            for name in files:
                os.remove(os.path.join(root, name))
            for name in dirs:
                os.rmdir(os.path.join(root, name))

    folder = "unit" if lang == "python" else "unit-java"

    os.system("java -jar swagger-codegen-cli-3.0.36.jar generate -i openapi.json -l " + lang + " -o " + folder)

    if lang == 'python':
        for filename in os.listdir(path_of_the_directory + '/models/'):
            f_path = os.path.join(path_of_the_directory, filename)
            if os.path.isfile(f_path):
                f = open(f_path, 'r')
                lineList = f.readlines()
                f.close

                # Re-open file here
                f2 = open(f_path, 'w')
                for line in lineList:
                    line = line.replace('#/components/schemas/', '')
                    f2.write(line)
                f2.close()

        f_path = path_of_the_directory + '/api/create_application_api.py'
        f = open(f_path, 'r')
        lineList = f.readlines()
        f.close

        # Re-open file here
        f2 = open(f_path, 'w')
        for line in lineList:
            line = line.replace('_create_application', 'create_application')
            f2.write(line)
        f2.close()

        change_default_header_for_python()
        change_bearer(default_bearer_auth)

    if lang == "java":
        change_default_header_for_java()

