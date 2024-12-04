# frozen_string_literal: true

require 'unit_openapi_ruby_sdk'
require_relative '../spec_helper'
require 'json'
require 'date'

RSpec.describe 'Application' do
  before do
    configure_tests
  end

  describe 'test attribute "id"' do
    let(:api_instance) { UnitOpenapiRubySdk::UnitApi.new(UnitOpenapiRubySdk::ApiClient.new(configuration)) }

    it 'should create an application' do
      request = { data: UnitOpenapiRubySdk::CreateIndividualApplication.new(type: 'individualApplication',
                                                                       attributes: UnitOpenapiRubySdk::CreateIndividualApplicationAttributes.new(
                                                                         ssn: '123456789', full_name: UnitOpenapiRubySdk::FullName.new({ first: 'John', last: 'Kenn' }), date_of_birth: Date.new(1989, 2, 1), address: ADDRESS, email: EMAIL, phone: PHONE,
                                                                         occupation: 'ArchitectOrEngineer', annual_income: 'UpTo10k', source_of_income: 'EmploymentOrPayrollIncome'
                                                                       )).to_hash }
      response = api_instance.create_application(request)
      expect(response.data.type).to eq('individualApplication')
    end
  end
end