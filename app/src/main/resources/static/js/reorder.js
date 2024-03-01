function updateNames(key, ...queries) {
    for (const query of queries) {
        const inputFields = document.querySelectorAll(query);
        inputFields.forEach(function(input, index) {
            input.name = key + '[' + index + ']';
            input.id = key + index;
        });
    }
}
