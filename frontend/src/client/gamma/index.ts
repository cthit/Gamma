import { Users } from "./users";
import axios, { AxiosInstance } from "axios";
import { Groups } from "./groups";
import { SuperGroups } from "./super-groups";
import { Types } from "./types";
import { Clients } from "./clients";
import { Posts } from "./posts";
import { AllowList } from "./allow-list";
import { ActivationCodes } from "./activation-codes";
import { CreateAccount } from "./create-account";
import { Admins } from "./admins";
import { Gdpr } from "./gdpr";
import { ApiKeys } from "./api-keys";
import { ClientAuthorities } from "./client-authorities";

export class GammaClient {
  private client: AxiosInstance = axios.create({
    baseURL: "/api/internal",
  });

  public readonly users: Users;
  public readonly groups: Groups;
  public readonly superGroups: SuperGroups;
  public readonly types: Types;
  public readonly clients: Clients;
  public readonly posts: Posts;
  public readonly allowList: AllowList;
  public readonly activationCodes: ActivationCodes;
  public readonly createAccount: CreateAccount;
  public readonly admins: Admins;
  public readonly gdpr: Gdpr;
  public readonly apiKeys: ApiKeys;
  public readonly clientAuthorities: ClientAuthorities;

  constructor() {
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response.status === 401) {
          window.location.href = import.meta.env.VITE_LOGIN_URL + "/login";
        }
      },
    );

    this.users = new Users(this.client);
    this.groups = new Groups(this.client);
    this.superGroups = new SuperGroups(this.client);
    this.types = new Types(this.client);
    this.clients = new Clients(this.client);
    this.posts = new Posts(this.client);
    this.allowList = new AllowList(this.client);
    this.activationCodes = new ActivationCodes(this.client);
    this.createAccount = new CreateAccount(this.client);
    this.admins = new Admins(this.client);
    this.gdpr = new Gdpr(this.client);
    this.apiKeys = new ApiKeys(this.client);
    this.clientAuthorities = new ClientAuthorities(this.client);
  }

  private static gammaClientInstance = new GammaClient();

  public static instance() {
    return this.gammaClientInstance;
  }
}
