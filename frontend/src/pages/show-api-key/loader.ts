import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type ShowApiKeyLoaderReturn = Awaited<ReturnType<typeof showApiKeyLoader>>;

export const useShowApiKeyLoaderData = (): ShowApiKeyLoaderReturn => {
  return useLoaderData() as ShowApiKeyLoaderReturn;
};

export const showApiKeyLoader = async (id: string) => {
  return await GammaClient.instance().apiKeys.getApiKey(id);
};
