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

export class GammaPostsClient {
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
}
