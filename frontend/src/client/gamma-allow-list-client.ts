import { AxiosInstance } from "axios";
import * as z from "zod";

const getAllowListValidation = z.array(z.string());

export class GammaAllowListClient {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getAllowList() {
    return getAllowListValidation.parse(
      (await this.client.get("/admin/users/allow-list")).data,
    );
  }
}
