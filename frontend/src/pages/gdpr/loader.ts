import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type GdprTrainedLoaderReturn = Awaited<ReturnType<typeof gdprTrainedLoader>>;

export const useGdprTrainedLoaderData = (): GdprTrainedLoaderReturn => {
  return useLoaderData() as GdprTrainedLoaderReturn;
};

export const gdprTrainedLoader = async () => {
  return {
    gdprTrained: await GammaClient.instance().gdpr.getGdprTrained(),
    users: await GammaClient.instance().users.getUsers(),
  };
};
