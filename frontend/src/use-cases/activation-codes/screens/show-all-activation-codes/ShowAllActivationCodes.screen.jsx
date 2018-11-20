import { DigitTranslations, DigitTable } from "@cthit/react-digit-components";
import React from "react";
import { Fill } from "../../../../common-ui/layout";
import translations from "./ShowAllActivationCodes.screen.translations.json";

const ShowAllActivationCodes = ({ activationCodes, text }) => (
    <Fill>
        <DigitTranslations
            translations={translations}
            uniquePath="ActivationCodes.Screen.ShowAllActivationCodes"
            render={text => (
                <DigitTable
                    titleText={text.ActivationCodes}
                    searchText={text.SearchForActivationCodes}
                    idProp="id"
                    startOrderBy="cid"
                    columnsOrder={["id", "cid", "code", "createdAt"]}
                    headerTexts={{
                        id: text.Id,
                        cid: text.Cid,
                        code: text.Code,
                        createdAt: text.CreatedAt,
                        __link: text.Details
                    }}
                    data={activationCodes.map(activationCode => {
                        return {
                            ...activationCode,
                            __link: "/activation-codes/" + activationCode.id
                        };
                    })}
                    emptyTableText={text.NoActivationCodes}
                />
            )}
        />
    </Fill>
);

export default ShowAllActivationCodes;
