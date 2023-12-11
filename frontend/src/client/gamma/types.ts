import { AxiosInstance } from "axios";
import * as z from "zod";

const getTypesValidation = z.array(z.string());

export class Types {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getTypes() {
    return getTypesValidation.parse(
      (await this.client.get("/admin/supergrouptype")).data,
    );
  }

  public async addType(type: string) {
    return this.client.post("/admin/supergrouptype", { type });
  }

  public async deleteType(type: string) {
    return this.client.delete("/admin/supergrouptype/" + type);
  }
}
