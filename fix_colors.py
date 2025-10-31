import os
import re

# Directory containing the entry files
entries_dir = r"D:\_My Projects\Jewel\src\main\resources\assets\jewelcharms\patchouli_books\guide\en_us\entries"

# Pattern to match $(#HEXCODE) and replace with $(HEXCODE)
pattern = r'\$\(#([0-9A-Fa-f]{6})\)'
replacement = r'$(\1)'

# Walk through all JSON files
for root, dirs, files in os.walk(entries_dir):
    for file in files:
        if file.endswith('.json'):
            filepath = os.path.join(root, file)

            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()

            # Replace all occurrences
            new_content = re.sub(pattern, replacement, content)

            if content != new_content:
                with open(filepath, 'w', encoding='utf-8', newline='\n') as f:
                    f.write(new_content)
                print(f"Fixed: {file}")

print("All files processed!")
