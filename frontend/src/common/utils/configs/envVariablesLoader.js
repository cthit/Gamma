const development = {
    frontend: "http://localhost:3000",
    backend: "http://localhost:8081/api"
};

const production = {
    frontend: "https://gamma.chalmers.it",
    backend: "https://gamma.chalmers.it/api"
};


function getFrontendUrl() {
    return isDevelopment() ? development.frontend : production.frontend;
}

function getBackendUrl() {
    return isDevelopment() ? development.backend : production.backend
}

function isDevelopment() {
    return process.env.NODE_ENV === "development"
}

export { getBackendUrl, getFrontendUrl };