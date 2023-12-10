import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type GdprTrainedLoaderReturn = Awaited<ReturnType<typeof gdprTrainedLoader>>;

export const useGdprTrainedLoaderData = (): GdprTrainedLoaderReturn => {
  return useLoaderData() as GdprTrainedLoaderReturn;
};

export const gdprTrainedLoader = async () => {
  return {
    gdprTrained: await GammaClient.getInstance().gdpr.getGdprTrained(),
    users: await GammaClient.getInstance().users.getUsers(),
  };
};
