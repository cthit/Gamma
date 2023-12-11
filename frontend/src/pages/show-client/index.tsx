import { useShowClientLoaderData } from "./loader";

export const ShowClientPage = () => {
  const client = useShowClientLoaderData();

  return (
    <div className={"mx-auto mt-4 w-fit"}>
      <ul className={"list-disc"}>
        <li>{client.clientId}</li>
        <li>{client.prettyName}</li>
        <li>{client.svDescription}</li>
        <li>{client.enDescription}</li>
        <li>{client.webServerRedirectUrl}</li>
        <li>Has Api key? {client.hasApiKey + ""}</li>
        {client.restriction !== null && (
          <>
            <li>Restricted to:</li>
            <ul className={"list-disc"}>
              {client.restriction.superGroups.map((superGroup) => (
                <li>{superGroup.prettyName}</li>
              ))}
            </ul>
          </>
        )}
      </ul>
    </div>
  );
};
