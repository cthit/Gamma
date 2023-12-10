import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type ActivationCodesLoaderReturn = Awaited<
  ReturnType<typeof activationCodesLoader>
>;

export const useActivationCodesLoaderData = (): ActivationCodesLoaderReturn => {
  return useLoaderData() as ActivationCodesLoaderReturn;
};

export const activationCodesLoader = async () => {
  return await GammaClient.getInstance().activationCodes.getActivationCodes();
};
