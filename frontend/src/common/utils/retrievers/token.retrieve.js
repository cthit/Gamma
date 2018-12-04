export default function token() {
    const sessionToken = sessionStorage.token;
    const localToken = localStorage.token;
    return sessionToken != null ? sessionToken : localToken;
}
