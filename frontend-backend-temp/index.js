/* External Requirements */
const express = require("express");
const http = require("http");
const bodyParser = require("body-parser");
const axios = require("axios");
const cors = require("cors");

/* Settings */
const PORT = 8082;
const SECRET = "secret";

/* Modules */
const app = express();
app.use(bodyParser.json());
const server = http.createServer(app);

app.use(cors());

/* Start Server */
server.listen(PORT);

app.post("/auth", (req, res) => {
    const code = req.body.code;
    const params = new URLSearchParams();
    const id = "7hAdUEtMo4MgFnA7ZoZ41ohTe1NNRoJmjL67Gf0NIrrBnauyhc";
    const secret = "LBoxmzohQOSRCz99uBhS0IjLglxUOaLRXJxIC8iWuHTWYCLLqo";
    params.append("grant_type", "authorization_code");
    params.append("client_id", id);
    params.append("redirect_uri", "http://localhost:3000/login");
    params.append("code", code);

    const c = Buffer.from(id + ":" + secret).toString("base64");

    axios
        .post("http://localhost:8081/api/oauth/token", params, {
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                Authorization: "Basic " + c
            }
        })
        .then(response => {
            console.log(response.data);
            res.send(response.data.access_token);
        })
        .catch(error => {
            console.log(error.response.data);
        });
});

/**
 *                             const params = new URLSearchParams();
                            params.append("grant_type", "authorization_code");
                            params.append("client_id", "this_is_a_client_id");
                            params.append("redirect_uri", "http://localhost:3000/login");
                            params.append("code", code);
                            console.log(params.toString());

                            const what = "this_is_a_client_id";


 */
