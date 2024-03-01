document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("authorizeButton").addEventListener("click", () => {
        const checkboxes = document.querySelectorAll("input[type='checkbox']");
        for (const checkbox of checkboxes) {
            checkbox.checked = true;
        }

        document.confirmationForm.submit();
    });
})
