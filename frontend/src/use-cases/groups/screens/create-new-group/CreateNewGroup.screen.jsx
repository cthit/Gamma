import { DigitLayout } from "@cthit/react-digit-components";
import React from "react";
import GroupForm from "../common-views/group-form";

const CreateNewGroup = ({ groupsAdd }) => (
  <DigitLayout.Fill>
    <DigitLayout.Center>
      <GroupForm
        onSubmit={(values, actions) => {
          console.log(values);
          groupsAdd(values);
        }}
        initialValues={{
          name: "",
          description: {
            sv: "",
            en: ""
          },
          email: "",
          func: {
            sv: "",
            en: ""
          }
        }}
      />
    </DigitLayout.Center>
  </DigitLayout.Fill>
);

export default CreateNewGroup;
