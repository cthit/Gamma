import * as z from "zod";
import { useRevalidator } from "react-router-dom";
import { GammaClient } from "../../client/gamma";
import { useCreateClientsLoaderData } from "./loader";

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
        userIds: z.array(z.string()),
        superGroupIds: z.array(z.string()),
        superGroupPosts: z.array(
          z
            .object({
              superGroupId: z.string(),
              postId: z.string(),
            })
            .strict(),
        ),
      })
      .strict(),
  })
  .strict();

export const CreateClientPage = () => {
  const { superGroups, users, posts } = useCreateClientsLoaderData();
  const revalidator = useRevalidator();

  const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    let form = Object.fromEntries(new FormData(e.currentTarget));

    //TODO:
    form = {
      ...form,
      restriction: {
        userIds: [],
        superGroupIds: [],
        superGroupPosts: [],
      },
    };

    const validatedForm = createClientValidation.parse(form);
    GammaClient.instance()
      .clients.createClient({
        ...validatedForm,
      })
      .then(() => revalidator.revalidate());
  };

  return (
    <div className={"outline flex flex-col gap-2 w-1/2 m-auto"}>
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

        <button type={"submit"}>Create client</button>
      </form>
    </div>
  );
};
