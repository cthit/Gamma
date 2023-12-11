import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type ApiKeysLoaderReturn = Awaited<ReturnType<typeof apiKeysLoader>>;

export const useApiKeysLoaderData = (): ApiKeysLoaderReturn => {
  return useLoaderData() as ApiKeysLoaderReturn;
};

export const apiKeysLoader = async () => {
  return {
    apiKeys: await GammaClient.instance().apiKeys.getApiKeys(),
    types: await GammaClient.instance().apiKeys.getApiTypes(),
  };
};
