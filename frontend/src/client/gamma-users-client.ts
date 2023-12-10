import * as z from "zod";
import { AxiosInstance } from "axios";

const getMeValidation = z
  .object({
    nick: z.string(),
    firstName: z.string(),
    lastName: z.string(),
    cid: z.string(),
    email: z.string(),
    id: z.string(),
    acceptanceYear: z.number(),
    userAgreement: z.boolean(),
    language: z.enum(["EN", "SV"]),
    isAdmin: z.boolean(),
    groups: z.array(
      z
        .object({
          group: z
            .object({
              id: z.string(),
              name: z.string(),
              prettyName: z.string(),
              superGroup: z
                .object({
                  id: z.string(),
                  version: z.number(),
                  prettyName: z.string(),
                  name: z.string(),
                  svDescription: z.string(),
                  enDescription: z.string(),
                  type: z.string(),
                })
                .strict(),
            })
            .strict(),
          post: z
            .object({
              id: z.string(),
              svName: z.string(),
              enName: z.string(),
              emailPrefix: z.string(),
              version: z.number(),
            })
            .strict(),
          unofficialPostName: z.string(),
        })
        .strict(),
    ),
  })
  .strict();

const getUsersValidation = z.array(
  z
    .object({
      cid: z.string(),
      nick: z.string(),
      firstName: z.string(),
      lastName: z.string(),
      id: z.string(),
      acceptanceYear: z.number(),
    })
    .strict(),
);

const getUserValidation = z
  .object({
    user: z
      .object({
        cid: z.string(),
        nick: z.string(),
        firstName: z.string(),
        lastName: z.string(),
        id: z.string(),
        acceptanceYear: z.number(),
      })
      .strict(),
    groups: z.array(
      z
        .object({
          group: z
            .object({
              id: z.string(),
              name: z.string(),
              prettyName: z.string(),
              superGroup: z
                .object({
                  id: z.string(),
                  version: z.number(),
                  prettyName: z.string(),
                  name: z.string(),
                  svDescription: z.string(),
                  enDescription: z.string(),
                  type: z.string(),
                })
                .strict(),
            })
            .strict(),
          post: z
            .object({
              id: z.string(),
              svName: z.string(),
              enName: z.string(),
              emailPrefix: z.string(),
              version: z.number(),
            })
            .strict(),
        })
        .strict(),
    ),
  })
  .strict();

export class GammaUsersClient {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getMe() {
    return getMeValidation.parse((await this.client.get("/users/me")).data);
  }

  public async getUsers() {
    return getUsersValidation.parse((await this.client.get("/users")).data);
  }

  public async getUser(id: string) {
    return getUserValidation.parse(
      (await this.client.get("/users/" + id)).data,
    );
  }
}
