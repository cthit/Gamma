function updateNames(key, ...queries) {
    if(queries.length === 1) {
        const inputFields = document.querySelectorAll(queries[0]);
        inputFields.forEach(function(input, index) {
            input.name = key + '[' + index + ']';
            input.id = key + index;
        });
    } else {
        for (const query of queries) {
            const inputFields = document.querySelectorAll(query);
            inputFields.forEach(function(input, index) {
                input.name = key + '[' + index + ']' + query;
                input.id = key + index;
            });
        }
    }
}
