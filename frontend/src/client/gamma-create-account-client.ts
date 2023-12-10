import { AxiosInstance } from "axios";

type CreateAccountInput = {
  cid: string;
  password: string;
  confirmPassword: string;
  nick: string;
  firstName: string;
  lastName: string;
  email: string;
  acceptanceYear: number;
  userAgreement: boolean;
};

export class GammaCreateAccountClient {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public enterCid(cid: string) {
    return this.client.post("/allow-list/activate-cid", {
      cid,
    });
  }

  public createAccount(account: CreateAccountInput) {
    return this.client.post("/users/create", account);
  }
}
