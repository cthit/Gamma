import { isRouteErrorResponse, useRouteError } from "react-router-dom";
import * as z from "zod";

export const ErrorPage = () => {
  const error = useRouteError();

  if (isRouteErrorResponse(error)) {
    switch (error.status) {
      case 500: {
        return (
          <div className={"w-screen"}>
            <div className={"w-fit mt-10 mx-auto"}>
              <p className={"text-base"}>Error: 500</p>
              <p className={"text-sm"}>The Office US, TODO</p>
            </div>
          </div>
        );
      }
      case 404: {
        return (
          <div className={"w-screen"}>
            <div className={"w-fit mt-10 mx-auto"}>
              <p className={"text-base"}>Error: 404</p>
              <p className={"text-sm"}>
                The Office US, Season 7, Episode 15, @07:23
              </p>
            </div>
          </div>
        );
      }
      case 403: {
        return (
          <div className={"w-screen"}>
            <div className={"w-fit mt-10 mx-auto"}>
              <p className={"text-base"}>Error: 403</p>
              <p className={"text-sm"}>
                The Office US, Season 6, Episode 1, @04:02
              </p>
            </div>
          </div>
        );
      }
    }
  } else if (error instanceof z.ZodError) {
    console.log(error);
    return <div>Unexpected response from backend. Please contact digIT </div>;
  }
  return <div>Something went wrong :(</div>;
};
