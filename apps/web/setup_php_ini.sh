#!/bin/bash

# Default XAMPP php.ini paths
DEFAULT_PATHS=(
    "/opt/lampp/etc/php.ini"
    "/Applications/XAMPP/xamppfiles/etc/php.ini"
    "/c/xampp/php/php.ini"
    "/mnt/c/xampp/php/php.ini"
    "/mnt/c/XAMPP/php/php.ini"
)

find_php_ini() {
    local custom_path="$1"
    
    # If custom path provided and exists, use it
    if [[ -n "$custom_path" && -f "$custom_path" ]]; then
        echo "$custom_path"
        return 0
    fi
    
    # Search default paths
    for path in "${DEFAULT_PATHS[@]}"; do
        if [[ -f "$path" ]]; then
            echo "Found php.ini at: $path" >&2
            echo "$path"
            return 0
        fi
    done
    
    echo "Error: php.ini not found in default XAMPP locations" >&2
    return 1
}

remove_extension_semicolons() {
    local file_path="$1"
    
    if [[ ! -f "$file_path" ]]; then
        echo "Error: File not found: $file_path" >&2
        return 1
    fi
    
    # Create backup
    local backup_path="${file_path}.backup"
    cp "$file_path" "$backup_path"
    echo "Backup created: $backup_path"
    
    # Extensions to uncomment
    local extensions=("curl" "gd" "mysqli" "pdo_mysql")
    local modified=false
    
    # Process file
    local temp_file=$(mktemp)
    
    while IFS= read -r line; do
        local original_line="$line"
        local found=false
        
        for ext in "${extensions[@]}"; do
            if [[ "$line" =~ ^[[:space:]]*\;[[:space:]]*extension[[:space:]]*=[[:space:]]*${ext}[[:space:]]*$ ]]; then
                # Remove leading semicolon and whitespace
                line=$(echo "$line" | sed 's/^[[:space:]]*;//')
                echo "Uncommented: $original_line"
                modified=true
                found=true
                break
            fi
        done
        
        echo "$line" >> "$temp_file"
    done < "$file_path"
    
    if [[ "$modified" == true ]]; then
        mv "$temp_file" "$file_path"
        echo "Successfully updated $file_path"
        echo "Please restart Apache/PHP service for changes to take effect."
    else
        rm "$temp_file"
        rm "$backup_path"
        echo "No matching extension lines found to uncomment."
    fi
    
    return 0
}

# Main execution
main() {
    local php_ini_path
    php_ini_path=$(find_php_ini "$1")
    
    if [[ $? -eq 0 ]]; then
        echo "Processing: $php_ini_path"
        remove_extension_semicolons "$php_ini_path"
        
        # Show PHP configuration info
        echo -e "\nPHP Configuration Information:"
        if command -v php &> /dev/null; then
            php --ini
        else
            echo "Warning: 'php' command not found in PATH. Please ensure PHP is installed and accessible."
        fi
    else
        echo "Usage: $0 [path_to_php.ini]"
        echo "If no path is provided, script will search default XAMPP locations."
        exit 1
    fi
}

main "$@"
