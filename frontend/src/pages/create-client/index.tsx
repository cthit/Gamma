import * as z from "zod";
import { useNavigate } from "react-router-dom";
import { GammaClient } from "../../client/gamma";
import { useCreateClientsLoaderData } from "./loader";
import { useRef, useState } from "react";

const createClientValidation = z
  .object({
    webServerRedirectUrl: z.string(),
    svDescription: z.string(),
    enDescription: z.string(),
    prettyName: z.string(),
    generateApiKey: z.coerce.boolean(),
    emailScope: z.coerce.boolean(),
    restriction: z
      .object({
        superGroupIds: z.array(z.string()),
      })
      .strict()
      .nullable(),
  })
  .strict();

export const CreateClientPage = () => {
  const { superGroups } = useCreateClientsLoaderData();
  const navigate = useNavigate();

  const [selectedSuperGroupIds, setSelectedSuperGroupIds] = useState<string[]>(
    [],
  );

  const superGroupSelectRef = useRef<HTMLSelectElement>(null);

  const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = Object.fromEntries(new FormData(e.currentTarget));

    const validatedForm = createClientValidation.parse({
      ...form,
      restriction:
        selectedSuperGroupIds.length > 0
          ? {
              superGroupIds: selectedSuperGroupIds,
            }
          : null,
    });

    GammaClient.instance()
      .clients.createClient(validatedForm)
      .then(() => {
        navigate("/clients");
      });
  };

  return (
    <div className={"outline flex flex-col gap-2 w-1/2 m-auto p-8"}>
      <form className={"contents"} onSubmit={onSubmit}>
        <input
          className={"outline"}
          type="text"
          name="prettyName"
          placeholder={"Pretty name"}
        />
        <input
          className={"outline"}
          type="text"
          name="svDescription"
          placeholder={"Swedish description"}
        />
        <input
          className={"outline"}
          type="text"
          name="enDescription"
          placeholder={"English description"}
        />
        <input
          className={"outline"}
          type="text"
          name="webServerRedirectUrl"
          placeholder={"Redirect url"}
        />
        <label htmlFor={"generateApiKey"}>Generate api key for client</label>
        <input
          type={"checkbox"}
          name={"generateApiKey"}
          id={"generateApiKey"}
          defaultChecked={false}
        />
        <label htmlFor={"emailScope"}>Include email scope</label>
        <input
          type={"checkbox"}
          name={"emailScope"}
          id={"emailScope"}
          defaultChecked={false}
        />
        <h3>Restricted to</h3>

        <p>Super groups</p>
        <ul className={"list-disc"}>
          {selectedSuperGroupIds.map((superGroupId) => (
            <li key={superGroupId}>
              {
                superGroups.find((superGroup) => superGroup.id === superGroupId)
                  ?.prettyName
              }
            </li>
          ))}
        </ul>
        <select ref={superGroupSelectRef} defaultValue={""}>
          {superGroups
            .filter(
              (superGroup) => !selectedSuperGroupIds.includes(superGroup.id),
            )
            .map((superGroup) => (
              <option key={superGroup.id} value={superGroup.id}>
                {superGroup.prettyName}
              </option>
            ))}
        </select>
        <button
          type={"button"}
          onClick={() => {
            if (superGroupSelectRef.current === null) {
              return;
            }

            const newSuperGroupId = superGroupSelectRef.current?.value;
            if (newSuperGroupId !== undefined) {
              setSelectedSuperGroupIds((ids) => [...ids, newSuperGroupId]);
            }
            superGroupSelectRef.current.value = "";
          }}
        >
          Add super group
        </button>

        <button type={"submit"}>Create client</button>
      </form>
    </div>
  );
};
