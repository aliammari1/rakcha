from bs4 import BeautifulSoup
import os
import re

# Define the directory that contains your Twig templates
directory = './templates/back'

# Iterate over all files in the directory
for filename in os.listdir(directory):
    if filename.endswith('.twig'):
        filepath = os.path.join(directory, filename)

        # Read the file with utf-8 encoding
        with open(filepath, 'r', encoding='utf-8') as file:
            content = file.read()

        # Parse the HTML
        soup = BeautifulSoup(content, 'html.parser')

        # Find all p and h1-h6 tags
        for tag in soup.find_all(['p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6','a','span','i']):
            # Add {{}} and auto_translate to the text
            tag.string = "{{ '" + tag.get_text(strip=True) + "'|auto_translate }}"

        # Write the file
        with open(filepath, 'w', encoding='utf-8') as file:
            file.write(str(soup))