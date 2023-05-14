import csv
from optparse import Values
import pandas as p

##list of all files in the current directory to perform operation
allfiles=['otnunit_aat_animals_8dc3_4d15_c278.csv',
'otnunit_aat_datacenter_attributes_8a94_cefd_f8a3.csv',
'otnunit_aat_detections_9062_5923_1394.csv',
'otnunit_aat_manmade_platform_0735_7c9f_329c.csv',
'otnunit_aat_project_attributes_f29c_fb21_23a3.csv',
'otnunit_aat_receivers_c595_05f4_68b2.csv',
'otnunit_aat_recover_offload_details_4b23_f002_f89a.csv',
'otnunit_aat_tag_releases_b793_03e7_a230.csv']
operationInfo={}
transmitterPd=p.DataFrame
##if null values for a column exceed 80% the column is deleted from the dataframe, csv
for file in allfiles:
    ##convert the csv file into dataframe using pandas library
    rowInfo={}
    datafr= p.read_csv(file,low_memory=False)
    ##droping the first row as it is alwasys empty
    datafr=datafr.drop([0],axis=0)

    ##constant set to fine tune maximum number of null values allowed for a column ,
    #  if the value exceeds , the whole column is dropped from the dataset currently set to 80 %
    percentageOfNullValuesAllowedForColumn=0.5

    ##getting a count of rows with null values grouped by column
    nullRowCount=datafr.isnull().sum()
    print(nullRowCount)
    totalRows=len(datafr.index)
    rowInfo['totalRows']=totalRows
    droppedColumns={}
    for data in nullRowCount.items():
        nullRatio=data[1]/totalRows
        if droppedColumns.get(data[0])==1:
            continue
        ##if the total rows are less than 500 , no columns will be dropped all na will be filled with empty string
        if totalRows<500:
            continue
        if nullRatio>=percentageOfNullValuesAllowedForColumn:
            datafr.drop(columns=data[0],inplace=True, axis=1)
            droppedColumns[data[0]]=1
    
    rowInfo['droppedColumns']=droppedColumns
    ##deleting all rows  which have empty Values , if total number of rows are greater then 500
    if totalRows<500:
        datafr.fillna("")
    else:
        datafr.dropna(inplace=True)

    ##dividing the file into pi
    if file=='otnunit_aat_datacenter_attributes_8a94_cefd_f8a3.csv':
        projectpidf=datafr.groupby(['datacenter_pi','datacenter_pi_organization','datacenter_pi_contact']).size().reset_index().rename(columns={0:'count'}).drop(columns='count')
        projectpidf.drop_duplicates(subset=['datacenter_pi'])
        datafr.drop(columns=['datacenter_pi_organization','datacenter_pi_contact'],inplace=True, axis=1)
        projectpidf.to_csv('pid.csv',index=False)
    ##dividing the file into pi
    if file=='otnunit_aat_project_attributes_f29c_fb21_23a3.csv':
        projectpidf=datafr.groupby(['project_pi','project_pi_organization','project_pi_contact']).size().reset_index().rename(columns={0:'count'}).drop(columns='count')
        projectpidf.drop_duplicates(subset=['project_pi'])
        datafr.drop(columns=['project_pi_organization','project_pi_contact'],inplace=True, axis=1)
        projectpidf.to_csv('pid.csv',index=False)
    
    ##divding the file into transmitter
    if file=='otnunit_aat_detections_9062_5923_1394.csv':
        transmitterPd=datafr.groupby(['transmitter_id','detection_transmittername','transmitter_codespace']).size().reset_index().rename(columns={0:'count'}).drop(columns='count')
        transmitterPd.columns=['transmitter_id','transmittername','transmitter_codespace']
        datafr.drop(columns=['transmitter_codespace','detection_transmittername'],inplace=True, axis=1)
        transmitterPd.to_csv('transmitter.csv',index=False)
    
    ##dividing the file into animal details
    if file=='otnunit_aat_animals_8dc3_4d15_c278.csv':
        animalPd=datafr.groupby(['animal_project_reference','vernacularname','scientificname','aphiaid','tsn']).size().reset_index().rename(columns={0:'count'}).drop(columns='count')
        datafr.drop(columns=['vernacularname','scientificname','aphiaid','tsn'],inplace=True, axis=1)
        animalPd.to_csv('animal.csv',index=False)
   ##all duplicates are dropped from the data
    if file=='otnunit_aat_recover_offload_details_4b23_f002_f89a.csv':
        datafr = datafr.drop_duplicates(subset='recovery_id', keep="first")

    rowInfo['deletedRows']=totalRows-len(datafr.index)
    rowInfo['dataLoss']=(totalRows-len(datafr.index))/totalRows
    operationInfo[file]=rowInfo
    newfilename="processd_"+file
    datafr.to_csv(newfilename,index=False)

##prinitng the data operation stats into csv file for analysing the data loss and inconsistency
dfcsv=p.DataFrame.from_dict(operationInfo)
dfcsv.to_csv ('gen.csv', index = False, header=True)


