import { AxiosInstance } from "axios";
import * as z from "zod";

const getPostsValidation = z.array(
  z
    .object({
      id: z.string(),
      emailPrefix: z.string(),
      svName: z.string(),
      enName: z.string(),
      version: z.number(),
    })
    .strict(),
);

const getPostValidation = z
    .object({
        id: z.string(),
        svName: z.string(),
        enName: z.string(),
        emailPrefix: z.string(),
        version: z.number(),
    })
    .strict();

type CreatePost = {
    svName: string;
    enName: string;
    emailPrefix: string;
};

export class Posts {
    private client: AxiosInstance;

    constructor(client: AxiosInstance) {
        this.client = client;
    }

    public async getPosts() {
        return getPostsValidation.parse((await this.client.get("/posts")).data);
    }

    public async getPost(id: string) {
        return getPostValidation.parse(
            (await this.client.get("/posts/" + id)).data,
        );
    }

    public async createPost(data: CreatePost) {
        return this.client.post("/admin/posts", data);
    }

    public async deletePost(id: string) {
        return this.client.delete("/admin/posts/" + id);
    }
}
