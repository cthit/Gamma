import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type ShowClientLoaderReturn = Awaited<ReturnType<typeof showClientLoader>>;

export const useShowClientLoaderData = (): ShowClientLoaderReturn => {
  return useLoaderData() as ShowClientLoaderReturn;
};

export const showClientLoader = async (id: string) => {
  return await GammaClient.getInstance().clients.getClient(id);
};
