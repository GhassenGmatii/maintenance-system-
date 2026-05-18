import os
import glob

base_dir = r"C:\Users\Ghassen\eclipse-workspace\maintenance_management_system\backend\src\main\java\com\itbs\maintenance"

java_files = glob.glob(os.path.join(base_dir, "**", "*.java"), recursive=True)

for file_path in java_files:
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Fix spring data jpa repository imports
    content = content.replace('org.springframework.data.jpa.depot', 'org.springframework.data.jpa.repository')
    content = content.replace('org.springframework.data.depot', 'org.springframework.data.repository')
    
    # Fix spring web config imports
    content = content.replace('org.springframework.web.servlet.configuration', 'org.springframework.web.servlet.config')

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

print("Fixes applied.")
