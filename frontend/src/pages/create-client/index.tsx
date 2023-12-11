import * as z from "zod";
import { useRevalidator } from "react-router-dom";
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
      .strict()
      .nullable(),
  })
  .strict();

export const CreateClientPage = () => {
  const { superGroups, users, posts } = useCreateClientsLoaderData();
  const revalidator = useRevalidator();

  const [selectedUserIds, setSelectedUserIds] = useState<string[]>([]);
  const [selectedSuperGroupIds, setSelectedSuperGroupIds] = useState<string[]>(
    [],
  );
  const [selectedSuperGroupPostsIds, setSelectedSuperGroupPostsIds] = useState<
    { postId: string; superGroupId: string }[]
  >([]);

  const userSelectRef = useRef<HTMLSelectElement>(null);
  const superGroupSelectRef = useRef<HTMLSelectElement>(null);
  const superGroup2SelectRef = useRef<HTMLSelectElement>(null);
  const postSelectRef = useRef<HTMLSelectElement>(null);

  const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = Object.fromEntries(new FormData(e.currentTarget));

    const validatedForm = createClientValidation.parse({
      ...form,
      restriction:
        selectedUserIds.length > 0 ||
        selectedSuperGroupIds.length > 0 ||
        selectedSuperGroupPostsIds.length > 0
          ? {
              userIds: selectedUserIds,
              superGroupIds: selectedSuperGroupIds,
              superGroupPosts: selectedSuperGroupPostsIds,
            }
          : null,
    });

    GammaClient.instance()
      .clients.createClient(validatedForm)
      .then(() => {
        revalidator.revalidate();
        setSelectedUserIds([]);
        setSelectedSuperGroupIds([]);
        setSelectedSuperGroupPostsIds([]);
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
        <p>Users</p>
        <ul className={"list-disc"}>
          {selectedUserIds.map((userId) => (
            <li key={userId}>
              {users.find((user) => user.id === userId)?.nick}
            </li>
          ))}
        </ul>

        <select ref={userSelectRef} defaultValue={""}>
          {users.map((user) => (
            <option key={user.id} value={user.id}>
              {user.nick}
            </option>
          ))}
        </select>
        <button
          type={"button"}
          disabled={superGroupSelectRef.current?.value === ""}
          onClick={() => {
            if (userSelectRef.current === null) {
              return;
            }

            const newUserId = userSelectRef.current?.value;
            if (newUserId !== undefined) {
              setSelectedUserIds((ids) => [...ids, newUserId]);
            }
            userSelectRef.current.value = "";
          }}
        >
          Add user
        </button>

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
          {superGroups.map((superGroup) => (
            <option key={superGroup.id} value={superGroup.id}>
              {superGroup.prettyName}
            </option>
          ))}
        </select>
        <button
          type={"button"}
          disabled={superGroupSelectRef.current?.value === ""}
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

        <p>Super group and post</p>
        <ul className={"list-disc"}>
          {selectedSuperGroupPostsIds.map(({ postId, superGroupId }) => (
            <li key={postId + superGroupId}>
              {
                superGroups.find((superGroup) => superGroup.id === superGroupId)
                  ?.prettyName
              }{" "}
              - {posts.find((post) => post.id === postId)?.enName}
            </li>
          ))}
        </ul>
        <select ref={superGroup2SelectRef} defaultValue={""}>
          {superGroups.map((superGroup) => (
            <option key={superGroup.id} value={superGroup.id}>
              {superGroup.prettyName}
            </option>
          ))}
        </select>
        <select ref={postSelectRef} defaultValue={""}>
          {posts.map((post) => (
            <option key={post.id} value={post.id}>
              {post.enName}
            </option>
          ))}
        </select>
        <button
          type={"button"}
          disabled={
            superGroup2SelectRef.current?.value === "" ||
            postSelectRef.current?.value === ""
          }
          onClick={() => {
            if (
              superGroup2SelectRef.current === null ||
              postSelectRef.current === null
            ) {
              return;
            }

            const newSuperGroupId = superGroup2SelectRef.current?.value;
            const newPostId = postSelectRef.current?.value;
            if (newSuperGroupId !== undefined && newPostId !== undefined) {
              setSelectedSuperGroupPostsIds((ids) => [
                ...ids,
                {
                  postId: newPostId,
                  superGroupId: newSuperGroupId,
                },
              ]);
            }
            superGroup2SelectRef.current.value = "";
            postSelectRef.current.value = "";
          }}
        >
          Add super group and post
        </button>

        <button type={"submit"}>Create client</button>
      </form>
    </div>
  );
};
