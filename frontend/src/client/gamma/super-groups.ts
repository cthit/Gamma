import { AxiosInstance } from "axios";
import * as z from "zod";

const getSuperGroupsValidation = z.array(
  z
    .object({
      id: z.string(),
      name: z.string(),
      prettyName: z.string(),
      svDescription: z.string(),
      enDescription: z.string(),
      type: z.string(),
      version: z.number(),
    })
    .strict(),
);

const getSuperGroupValidation = z
  .object({
    id: z.string(),
    name: z.string(),
    prettyName: z.string(),
    svDescription: z.string(),
    enDescription: z.string(),
    type: z.string(),
    version: z.number(),
  })
  .strict();

type CreateSuperGroup = {
  name: string;
  prettyName: string;
  svDescription: string;
  enDescription: string;
  type: string;
};

export class SuperGroups {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getSuperGroups() {
    return getSuperGroupsValidation.parse(
      (await this.client.get("/super-groups")).data,
    );
  }

  public async getSuperGroup(id: string) {
    return getSuperGroupValidation.parse(
      (await this.client.get("/super-groups/" + id)).data,
    );
  }

  public async createSuperGroup(data: CreateSuperGroup) {
    return this.client.post("/admin/super-groups", data);
  }

  public async deleteSuperGroup(id: string) {
    return this.client.delete("/admin/super-groups/" + id);
  }
}
