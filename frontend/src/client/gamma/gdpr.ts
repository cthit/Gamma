import { AxiosInstance } from "axios";
import * as z from "zod";

const getGdprTrainedValidation = z.array(z.string());

export class Gdpr {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getGdprTrained() {
    return getGdprTrainedValidation.parse(
      (await this.client.get("/admin/gdpr")).data,
    );
  }

  public async setGdprTrained(userId: string, gdprTrained: boolean) {
    return this.client.put("/admin/gdpr/" + userId, { gdpr: gdprTrained });
  }
}
