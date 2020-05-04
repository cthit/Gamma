import React, { useEffect, useState } from "react";
import { addDays } from "date-fns";
import {
    DigitCRUD,
    useDigitTranslations,
    DigitButton,
    DigitLayout
} from "@cthit/react-digit-components";
import translations from "./Groups.translations";
import { getGroup, getGroupsMinified } from "../../api/groups/get.groups.api";
import {
    GROUP_BECOMES_ACTIVE,
    GROUP_BECOMES_INACTIVE,
    GROUP_MEMBERS,
    GROUP_ID,
    GROUP_NAME,
    GROUP_PRETTY_NAME,
    GROUP_SUPER_GROUP,
    GROUP_EMAIL,
    GROUP_DESCRIPTION_EN,
    GROUP_DESCRIPTION_SV,
    GROUP_FUNCTION_EN,
    GROUP_FUNCTION_SV,
    GROUP_NO_ACCOUNT_MEMBERS
} from "../../api/groups/props.groups.api";
import { editGroup } from "../../api/groups/put.groups.api";
import { getSuperGroups } from "../../api/super-groups/get.super-groups.api";
import { addGroup } from "../../api/groups/post.groups.api";
import DisplayMembersTable from "../../common/elements/display-members-table";
import { useHistory } from "react-router-dom";
import useGammaIsAdmin from "../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import { deleteGroup } from "../../api/groups/delete.groups.api";
import FourOFour from "../four-o-four";
import FiveZeroZero from "../../app/elements/five-zero-zero";
import {
    initialValues,
    keysOrder,
    keysText,
    readAllKeysOrder,
    readOneKeysOrder,
    keysComponentData,
    validationSchema,
    updateKeysOrder
} from "./Groups.options";
import InsufficientAccess from "../../common/views/insufficient-access";

const Groups = () => {
    const [text] = useDigitTranslations(translations);
    const admin = useGammaIsAdmin();
    const [superGroups, setSuperGroups] = useState([]);
    const history = useHistory();

    useEffect(() => {
        getSuperGroups().then(response => {
            setSuperGroups(response.data);
        });
    }, []);

    if (superGroups.length === 0) {
        return null;
    }

    return (
        <DigitCRUD
            formInitialValues={initialValues()}
            formValidationSchema={validationSchema(text)}
            formComponentData={keysComponentData(text, superGroups)}
            keysOrder={keysOrder()}
            readOneKeysOrder={readOneKeysOrder()}
            readAllKeysOrder={readAllKeysOrder()}
            updateKeysOrder={updateKeysOrder()}
            keysText={keysText(text)}
            canDelete={() => admin}
            canUpdate={() => admin} //|| inGroup(user, group)}
            name={"groups"}
            path={"/groups"}
            readAllRequest={getGroupsMinified}
            readOneRequest={getGroup}
            deleteRequest={deleteGroup}
            updateRequest={(id, data) => {
                const becomesActive = addDays(data.becomesActive, 1);
                const becomesInactive = addDays(data.becomesInactive, 1);

                return editGroup(id, {
                    name: data.name,
                    function: {
                        sv: data.functionSv,
                        en: data.functionEn
                    },
                    description: {
                        sv: data.descriptionSv,
                        en: data.descriptionEn
                    },
                    email: data.email,
                    superGroup: data.superGroup,
                    prettyName: data.prettyName,
                    becomesActive: becomesActive,
                    becomesInactive: becomesInactive
                });
            }}
            createRequest={
                admin
                    ? data => {
                          const becomesActive = addDays(
                              data[GROUP_BECOMES_ACTIVE],
                              1
                          );
                          const becomesInactive = addDays(
                              data[GROUP_BECOMES_INACTIVE],
                              1
                          );

                          return addGroup({
                              name: data[GROUP_NAME],
                              function: {
                                  sv: data[GROUP_FUNCTION_SV],
                                  en: data[GROUP_FUNCTION_EN]
                              },
                              description: {
                                  sv: data[GROUP_DESCRIPTION_SV],
                                  en: data[GROUP_DESCRIPTION_EN]
                              },
                              email: data[GROUP_EMAIL],
                              superGroup: data[GROUP_SUPER_GROUP],
                              prettyName: data[GROUP_PRETTY_NAME],
                              becomesActive,
                              becomesInactive
                          });
                      }
                    : null
            }
            tableProps={{
                orderBy: GROUP_NAME,
                startOrderBy: GROUP_NAME,
                titleText: text.Groups,
                search: true,
                flex: "1",
                startOrderByDirection: "asc",
                size: { minWidth: "288px" },
                padding: "0px"
            }}
            idProp={GROUP_ID}
            detailsRenderCardEnd={data =>
                admin ? (
                    <>
                        <div style={{ marginTop: "8px" }} />
                        <DigitLayout.Center>
                            <DigitButton
                                outlined
                                text={"Edit members"}
                                onClick={() =>
                                    history.push("/members/" + data.id)
                                }
                            />
                        </DigitLayout.Center>
                    </>
                ) : null
            }
            detailsRenderEnd={data => (
                <DigitLayout.Row flexWrap={"wrap"} justifyContent={"center"}>
                    <DisplayMembersTable
                        margin={{ top: "16px", right: "8px" }}
                        noUsersText={text.NoGroupMembers}
                        users={data[GROUP_MEMBERS]}
                    />

                    {data[GROUP_NO_ACCOUNT_MEMBERS].length > 0 && (
                        <DisplayMembersTable
                            title={text.NoAccountMembers}
                            margin={{ top: "16px", left: "8px" }}
                            users={data[GROUP_NO_ACCOUNT_MEMBERS].map(
                                member => ({
                                    nick: member.cid,
                                    ...member
                                })
                            )}
                        />
                    )}
                </DigitLayout.Row>
            )}
            dateProps={[GROUP_BECOMES_ACTIVE, GROUP_BECOMES_INACTIVE]}
            createButtonText={text.Create + " " + text.Group}
            updateTitle={group => text.Update + " " + group[GROUP_PRETTY_NAME]}
            createTitle={text.CreateGroup}
            detailsTitle={group => group[GROUP_PRETTY_NAME]}
            statusRenders={{
                403: () => <InsufficientAccess />,
                404: () => <FourOFour />,
                500: (error, reset) => <FiveZeroZero reset={reset} />
            }}
        />
    );
};

export default Groups;
