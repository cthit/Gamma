import {
    DigitButton,
    DigitTranslations,
    DigitDisplayData
} from "@cthit/react-digit-components";
import React from "react";
import {
    GammaCard,
    GammaCardBody,
    GammaCardButtons,
    GammaLink
} from "../../../../common-ui/design";
import { Center, Fill, Spacing } from "../../../../common-ui/layout";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import translations from "./ShowWebsiteDetails.screen.translations.json";

const ShowWebsiteDetails = ({
    website,
    gammaDialogOpen,
    toastOpen,
    redirectTo,
    websitesDelete
}) => (
    <IfElseRendering
        test={website != null}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                uniquePath="Websites.Screen.ShowWebsiteDetails"
                render={text => (
                    <Fill>
                        <Center>
                            <GammaCard minWidth="300px" maxWidth="600px">
                                <GammaCardBody>
                                    <DigitDisplayData
                                        data={website}
                                        keysText={{
                                            id: text.Id,
                                            name: text.Name,
                                            prettyName: text.PrettyName
                                        }}
                                        keysOrder={["id", "name", "prettyName"]}
                                    />
                                </GammaCardBody>
                                <GammaCardButtons reverseDirection>
                                    <GammaLink
                                        to={"/websites/" + website.id + "/edit"}
                                    >
                                        <DigitButton
                                            text={text.EditWebsite}
                                            primary
                                            raised
                                        />
                                    </GammaLink>
                                    <Spacing />
                                    <DigitButton
                                        onClick={() => {
                                            gammaDialogOpen({
                                                title:
                                                    text.WouldYouLikeToDelete +
                                                    " " +
                                                    website.prettyName,
                                                confirmButtonText:
                                                    text.DeleteWebsite,
                                                cancelButtonText: text.Cancel,
                                                onConfirm: () => {
                                                    websitesDelete(website.id)
                                                        .then(response => {
                                                            toastOpen({
                                                                text:
                                                                    website.prettyName +
                                                                    " " +
                                                                    text.Deleted
                                                            });
                                                            redirectTo(
                                                                "/websites"
                                                            );
                                                        })
                                                        .catch(error => {
                                                            toastOpen({
                                                                text:
                                                                    text.SomethingWentWrong
                                                            });
                                                        });
                                                }
                                            });
                                        }}
                                        text={text.DeleteWebsite}
                                    />
                                </GammaCardButtons>
                            </GammaCard>
                        </Center>
                    </Fill>
                )}
            />
        )}
    />
);

export default ShowWebsiteDetails;
