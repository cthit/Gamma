import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type ClientsLoaderReturn = Awaited<ReturnType<typeof clientsLoader>>;

export const useClientsLoaderData = (): ClientsLoaderReturn => {
  return useLoaderData() as ClientsLoaderReturn;
};

export const clientsLoader = async () => {
  return await GammaClient.getInstance().clients.getClients();
};
