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

// Names the rows of a client authority form. Only looks inside the given form,
// since the page can show the create form and several edit forms at once.
function updateAuthorityNames(form) {
    form.querySelectorAll('.authority-super-group').forEach(function(select, index) {
        select.name = 'superGroups[' + index + ']';
    });
    form.querySelectorAll('.authority-super-group-post-row').forEach(function(row, index) {
        row.querySelector('.superGroupId').name = 'superGroupPosts[' + index + '].superGroupId';
        row.querySelector('.postId').name = 'superGroupPosts[' + index + '].postId';
    });
    form.querySelectorAll('.authority-user').forEach(function(select, index) {
        select.name = 'users[' + index + ']';
    });
}
