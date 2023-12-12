import { useShowClientLoaderData } from "./loader";
import { useRevalidator } from "react-router-dom";
import * as z from "zod";
import { GammaClient } from "../../client/gamma";
import { ManageAuthority } from "./ManageAuthority";

const createAuthorityNameValidation = z
  .object({
    authorityName: z.string(),
  })
  .strict();

export const ShowClientPage = () => {
  const { client, authorities } = useShowClientLoaderData();
  const revalidator = useRevalidator();

  const createAuthorityName = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = Object.fromEntries(new FormData(e.currentTarget));

    const validatedForm = createAuthorityNameValidation.parse(form);
    GammaClient.instance()
      .clientAuthorities.createAuthority(
        client.clientUid,
        validatedForm.authorityName,
      )
      .then(() => revalidator.revalidate());
  };

  return (
    <div className={"mx-auto mt-4 w-fit"}>
      <ul className={"list-disc"}>
        <li>{client.clientId}</li>
        <li>{client.prettyName}</li>
        <li>{client.svDescription}</li>
        <li>{client.enDescription}</li>
        <li>{client.webServerRedirectUrl}</li>
        <li>Has Api key? {client.hasApiKey + ""}</li>
      </ul>
      {client.restriction !== null && (
        <>
          <p className={"mt-4"}>Restricted to:</p>
          <ul className={"list-disc"}>
            {client.restriction.superGroups.map((superGroup) => (
              <li key={superGroup.id}>{superGroup.prettyName}</li>
            ))}
          </ul>
        </>
      )}
      <h2 className={"mt-4"}>Authorities</h2>
      <ul className={"list-disc"}>
        {authorities.map((authority) => (
          <div key={authority.authorityName} className={"p-5 outline-dashed"}>
            <li>{authority.authorityName}</li>
            <ManageAuthority authorityName={authority.authorityName} />
          </div>
        ))}
      </ul>
      <p className={"mt-4"}>Add new authority name</p>
      <div className={"outline flex flex-col gap-2 mt-4"}>
        <form className={"contents"} onSubmit={createAuthorityName}>
          <input
            className={"outline"}
            type="text"
            name="authorityName"
            placeholder={"Authority Name"}
          />
          <button type={"submit"}>Add authority name</button>
        </form>
      </div>
    </div>
  );
};
