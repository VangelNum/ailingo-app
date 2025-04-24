window.jsSelectImage = function(callback) {
    // Create a hidden file input element dynamically
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*'; // Specify accepted file types (e.g., images)
    input.style.display = 'none'; // Hide the input element

    // Add an event listener for when a file is selected
    input.onchange = function(event) {
        // Remove the input element from the DOM after selection/cancellation
        // This cleans up the document.
        document.body.removeChild(input);

        const file = event.target.files && event.target.files[0]; // Get the selected file

        if (!file) {
            // No file was selected (user likely cancelled the dialog)
            callback(null);
            return;
        }

        // Use FileReader to read the file content
        const reader = new FileReader();

        // Set up the load end event listener
        reader.onloadend = function() {
            if (reader.readyState === FileReader.DONE && reader.result) {
                // File reading finished successfully
                // reader.result contains the data URL (e.g., "data:image/jpeg;base64,/9j...")
                // We only want the Base64 part after the comma.
                const dataUrl = reader.result;
                const base64String = dataUrl.substring(dataUrl.indexOf(',') + 1);

                callback(base64String); // Pass ONLY the Base64 part
            } else {
                console.error("Error reading file:", reader.error);
                callback(null);
            }
        };

        // Set up the error event listener for reading
        reader.onerror = function() {
             console.error("Error reading file:", reader.error);
             callback(null);
        };

        // Start reading the file as a Data URL (Base64)
        reader.readAsDataURL(file);
    };

    // Append the input to the body (it must be in the DOM to trigger the click)
    document.body.appendChild(input);

    // Programmatically trigger the click event to open the file selection dialog
    input.click();
};