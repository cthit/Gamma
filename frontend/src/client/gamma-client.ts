import { GammaUsersClient } from "./gamma-users-client";
import axios, { AxiosInstance } from "axios";
import { GammaGroupsClient } from "./gamma-groups-client";
import { GammaSuperGroupsClient } from "./gamma-super-groups-client";
import { GammaTypesClient } from "./gamma-types-client";
import { GammaClientsClient } from "./gamma-clients-client";
import { GammaPostsClient } from "./gamma-posts-client";
import { GammaAllowListClient } from "./gamma-allow-list-client";
import { GammaActivationCodesClient } from "./gamma-activation-codes-client";
import { GammaCreateAccountClient } from "./gamma-create-account-client";
import { GammaAdminsClient } from "./gamma-admins-client";

export class GammaClient {
  private client: AxiosInstance = axios.create({
    baseURL: "/api/internal",
  });

  public readonly users: GammaUsersClient;
  public readonly groups: GammaGroupsClient;
  public readonly superGroups: GammaSuperGroupsClient;
  public readonly types: GammaTypesClient;
  public readonly clients: GammaClientsClient;
  public readonly posts: GammaPostsClient;
  public readonly allowList: GammaAllowListClient;
  public readonly activationCodes: GammaActivationCodesClient;
  public readonly createAccount: GammaCreateAccountClient;
  public readonly admins: GammaAdminsClient;

  constructor() {
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response.status === 401) {
          window.location.href = "http://gamma:8081/api/login";
        }
      },
    );

    this.users = new GammaUsersClient(this.client);
    this.groups = new GammaGroupsClient(this.client);
    this.superGroups = new GammaSuperGroupsClient(this.client);
    this.types = new GammaTypesClient(this.client);
    this.clients = new GammaClientsClient(this.client);
    this.posts = new GammaPostsClient(this.client);
    this.allowList = new GammaAllowListClient(this.client);
    this.activationCodes = new GammaActivationCodesClient(this.client);
    this.createAccount = new GammaCreateAccountClient(this.client);
    this.admins = new GammaAdminsClient(this.client);
  }

  private static gammaClientInstance = new GammaClient();

  public static getInstance() {
    return this.gammaClientInstance;
  }
}
