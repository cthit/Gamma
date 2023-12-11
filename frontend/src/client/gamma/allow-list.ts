import { AxiosInstance } from "axios";
import * as z from "zod";

const getAllowListValidation = z.array(z.string());

export class AllowList {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getAllowList() {
    return getAllowListValidation.parse(
        (await this.client.get("/admin/users/allow-list")).data,
    );
  }

  public async allow(cid: string) {
    return this.client.post("/admin/users/allow-list", {cid});
  }

  public async remove(cid: string) {
    return this.client.delete("/admin/users/allow-list/" + cid);
  }
}
