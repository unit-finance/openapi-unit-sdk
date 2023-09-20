import os
import json

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


def change_bearer(bearer=python_bearer_auth):
    with open('openapi.json', encoding='utf8') as file:
        j = json.load(file)

        j["components"]["securitySchemes"]["bearerAuth"] = bearer

    with open('openapi.json', 'w', encoding='utf8') as file:
        json.dump(j, file, indent=4, ensure_ascii=False)


# if flag is not python
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
        # for line in lineList:
        #     line = line.replace('./schemas/', '')
        #     f2.write(line)
        f2.close()

path_of_the_directory = "./unit/swagger_client"
for root, dirs, files in os.walk(path_of_the_directory, topdown=False):
    for name in files:
        os.remove(os.path.join(root, name))
    for name in dirs:
        os.rmdir(os.path.join(root, name))

os.system("java -jar swagger-codegen-cli-3.0.36.jar generate -i openapi.json -l python -o unit")

path_of_the_directory = './unit/swagger_client/models/'

for filename in os.listdir(path_of_the_directory):
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

f_path = "unit/swagger_client/api/create_application_api.py"
f = open(f_path, 'r')
lineList = f.readlines()
f.close

# Re-open file here
f2 = open(f_path, 'w')
for line in lineList:
    line = line.replace('_create_application', 'create_application')
    f2.write(line)
f2.close()


change_bearer(default_bearer_auth)



