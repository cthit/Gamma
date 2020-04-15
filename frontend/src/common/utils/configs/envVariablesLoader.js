const development = {
    backend: "http://localhost:8081/api"
};

const production = {
    backend: "https://gamma.chalmers.it/api"
};

function getBackendUrl() {
    return isDevelopment() ? development.backend : production.backend;
}

function isDevelopment() {
    return process.env.NODE_ENV === "development";
}

export { getBackendUrl };
