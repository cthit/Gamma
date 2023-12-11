import { AxiosInstance } from "axios";
import * as z from "zod";

const getAdminsValidation = z.array(z.string());

export class Admins {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getAdmins() {
    return getAdminsValidation.parse(
      (await this.client.get("/admin/users/admins")).data,
    );
  }

  public async setAdmin(userId: string, isAdmin: boolean) {
    return this.client.put("/admin/users/admins/" + userId, { isAdmin });
  }
}
