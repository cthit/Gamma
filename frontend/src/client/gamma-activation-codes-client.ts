import { AxiosInstance } from "axios";
import * as z from "zod";

const getActivationCodesValidation = z.array(
  z
    .object({
      cid: z.string(),
      token: z.string(),
      createdAt: z.string(),
    })
    .strict(),
);

export class GammaActivationCodesClient {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getActivationCodes() {
    return getActivationCodesValidation.parse(
      (await this.client.get("/admin/user-activation")).data,
    );
  }
}
