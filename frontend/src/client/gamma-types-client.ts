import { AxiosInstance } from "axios";
import * as z from "zod";

const getTypesValidation = z.array(z.string());

export class GammaTypesClient {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getTypes() {
    return getTypesValidation.parse(
      (await this.client.get("/admin/supergrouptype")).data,
    );
  }
}
