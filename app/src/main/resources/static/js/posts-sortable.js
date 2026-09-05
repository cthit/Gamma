document.addEventListener("click", function (event) {
    const button = event.target.closest("button[data-post-move]");
    if (!button) return;
    const row = button.closest("tr");
    const rows = row.closest(".post-order");
    if (!rows) return;

    if (button.dataset.postMove === "up" && row.previousElementSibling) {
        rows.insertBefore(row, row.previousElementSibling);
    } else if (button.dataset.postMove === "down" && row.nextElementSibling) {
        rows.insertBefore(row.nextElementSibling, row);
    }
    button.focus();
});
