import React from "react";

import { Fill, Center } from "../../../../common-ui/layout";

import EditGroupInformation from "../common-views/edit-group-information";

const CreateNewGroup = ({ groupsAdd }) => (
  <Fill>
    <Center>
      <EditGroupInformation
        onSubmit={(values, actions) => {
          console.log(groupsAdd);
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
    </Center>
  </Fill>
);

export default CreateNewGroup;
