import { FC, useRef } from "react";
import { useShowClientLoaderData } from "./loader";
import { GammaClient } from "../../client/gamma";
import * as z from "zod";
import { useRevalidator } from "react-router-dom";

const addSuperGroupToAuthorityNameValidation = z
  .object({
    superGroupId: z.string(),
  })
  .strict();

export const ManageAuthority: FC<{ authorityName: string }> = ({
  authorityName,
}) => {
  const { client, superGroups, authorities } = useShowClientLoaderData();
  const selectSuperGroupRef = useRef<HTMLSelectElement>(null);
  const revalidator = useRevalidator();

  const addSuperGroupToAuthorityName = (
    e: React.FormEvent<HTMLFormElement>,
  ) => {
    e.preventDefault();
    const form = Object.fromEntries(new FormData(e.currentTarget));

    const validatedForm = addSuperGroupToAuthorityNameValidation.parse(form);
    GammaClient.instance()
      .clientAuthorities.addSuperGroupToAuthority({
        clientUid: client.clientUid,
        authorityName,
        superGroupId: validatedForm.superGroupId,
      })
      .then(() => revalidator.revalidate());
  };

  const currentAuthority = authorities.find(
    (authority) => authority.authorityName === authorityName,
  );

  if (currentAuthority === undefined) {
    return null;
  }

  return (
    <>
      TODO: Add users and supergroup-posts combo
      <div className={"flex flex-col gap-4 outline-dashed p-4"}>
        Current super groups
        <ul className={"list-disc"}>
          {currentAuthority.superGroups.map((superGroup) => (
            <li key={superGroup.id}>{superGroup.prettyName}</li>
          ))}
        </ul>
        <form className="contents" onSubmit={addSuperGroupToAuthorityName}>
          <select name={"superGroupId"} ref={selectSuperGroupRef}>
            {superGroups.map((superGroup) => (
              <option key={superGroup.id} value={superGroup.id}>
                {superGroup.prettyName}
              </option>
            ))}
          </select>
          <button type={"submit"}>Add super group</button>
        </form>
      </div>
    </>
  );
};
